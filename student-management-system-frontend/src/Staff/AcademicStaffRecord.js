import React, {useEffect, useState} from 'react';
import {
  Button,
  Card,
  Col,
  Descriptions,
  Form,
  Input,
  Layout,
  message,
  Modal,
  Row,
  Select,
  Statistic,
  Table
} from 'antd';
import {DeleteOutlined, EditOutlined, ReloadOutlined} from '@ant-design/icons';
import api from "../api";

const {Content} = Layout;
const {Option} = Select;

const AcademicStaffRecord = ({staffId, setSelectedKey, setStaffInfo, readOnly = false, showModules = false}) => {
  const [staff, setStaff] = useState(null);
  const [modules, setModules] = useState([]);
  const [moduleStats, setModuleStats] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [titles, setTitles] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [editForm] = Form.useForm();

  const fetchStaffData = async () => {
    if (!staffId) return;
    setLoading(true);
    try {
      // Fetch staff details
      const response = await api.get(`/api/staff/${staffId}`);
      const staffData = response.data.data;
      setStaff(staffData);

      // Set staff info for breadcrumb
      setStaffInfo({
        id: staffData.id,
        firstName: staffData.firstName,
        lastName: staffData.lastName
      });

      // Fetch modules for this staff using their full name
      const modulesResponse = await api.get('/api/module/list', {
        params: {
          current: 1,
          size: 100,
          staffFirstName: staffData.firstName,
          staffLastName: staffData.lastName
        }
      });

      // Set modules data
      const moduleRecords = modulesResponse.data?.data?.records || [];
      const filteredModules = moduleRecords.filter(module =>
          module.staffFirstName === staffData.firstName &&
          module.staffLastName === staffData.lastName
      );
      setModules(filteredModules);

      // Fetch statistics for each module
      const statsPromises = filteredModules.map(module =>
          api.get('/api/record', {
            params: {
              moduleCode: module.code,
              current: 1,
              size: 100
            }
          })
      );

      const statsResponses = await Promise.all(statsPromises);
      const moduleStatistics = statsResponses.map((response, index) => {
        const records = response.data?.data?.records || [];
        const totalScore = records.reduce((sum, record) => sum + (record.averageScore || 0), 0);
        const totalPassRate = records.reduce((sum, record) => sum + (record.passRate || 0), 0);
        const totalCandidates = records.reduce((sum, record) => sum + (record.numberOfCandidates || 0), 0);

        return {
          moduleCode: filteredModules[index].code,
          averageScore: records.length ? totalScore / records.length : 0,
          passRate: records.length ? totalPassRate / records.length : 0,
          studentCount: totalCandidates
        };
      });
      setModuleStats(moduleStatistics);

    } catch (error) {
      message.error('Failed to fetch staff data');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStaffData();
  }, [staffId]);

  const calculateStatistics = () => {
    if (!moduleStats.length) return null;

    const totalModules = moduleStats.length;
    const totalStudents = moduleStats.reduce((sum, m) => sum + (m.studentCount || 0), 0);
    const averageScore = moduleStats.reduce((sum, m) => sum + (m.averageScore || 0), 0) / totalModules;
    const averagePassRate = moduleStats.reduce((sum, m) => sum + (m.passRate || 0), 0) / totalModules;

    return {
      totalModules,
      totalStudents,
      averageScore: averageScore.toFixed(1),
      averagePassRate: (averagePassRate * 100).toFixed(1)
    };
  };

  const moduleColumns = [
    {
      title: 'Module Code',
      dataIndex: 'code',
      key: 'code',
      width: 120
    },
    {
      title: 'Module Name',
      dataIndex: 'name',
      key: 'name',
      width: 250
    },
    {
      title: 'Credits',
      dataIndex: 'credits',
      key: 'credits',
      width: 100
    },
    {
      title: 'MNC',
      dataIndex: 'mnc',
      key: 'mnc',
      width: 100,
      render: (mnc) => mnc ? 'Yes' : 'No'
    },
    {
      title: 'Students',
      key: 'studentCount',
      width: 120,
      render: (_, record) => {
        const stats = moduleStats.find(s => s.moduleCode === record.code);
        return stats?.studentCount || 0;
      }
    },
    {
      title: 'Average Score',
      key: 'averageScore',
      width: 120,
      render: (_, record) => {
        const stats = moduleStats.find(s => s.moduleCode === record.code);
        return stats?.averageScore ? `${stats.averageScore.toFixed(1)}%` : '-';
      }
    },
    {
      title: 'Pass Rate',
      key: 'passRate',
      width: 120,
      render: (_, record) => {
        const stats = moduleStats.find(s => s.moduleCode === record.code);
        return stats?.passRate ? `${(stats.passRate * 100).toFixed(1)}%` : '-';
      }
    }
  ];

  const handleEditSubmit = async () => {
    try {
      const values = await editForm.validateFields();
      setSubmitting(true);

      const response = await api.put(`/api/staff/edit/${staffId}`, {
        ...values,
        title: values.title?.toString(),
        department: values.department?.toString()
      });

      if (response.data?.code === 200) {
        message.success('Staff details updated successfully');
        setIsEditModalOpen(false);
        fetchStaffData();
      } else {
        throw new Error(response.data?.message || 'Failed to update staff');
      }
    } catch (error) {
      console.error('Error updating staff:', error);
      message.error(error.response?.data?.message || 'Failed to update staff details');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async () => {
    try {
      const response = await api.delete(`/api/staff/delete/${staffId}`);
      if (response.data?.code === 200) {
        message.success('Staff deleted successfully');
        setIsDeleteModalOpen(false);
        setSelectedKey('4'); // Navigate back to staff list
      } else {
        throw new Error(response.data?.message || 'Failed to delete staff');
      }
    } catch (error) {
      console.error('Error deleting staff:', error);
      message.error(error.response?.data?.message || 'Failed to delete staff');
    }
  };

  return (
    <Content style={{ margin: '24px 16px 0', padding: 24, background: '#fff' }}>
      {/* Header Section */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Staff Details</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button icon={<ReloadOutlined />} onClick={fetchStaffData} loading={loading} />
          {!readOnly && (
              <>
                <Button
                    type="primary"
                    icon={<EditOutlined/>}
                    onClick={() => {
                      editForm.setFieldsValue(staff);
                      setIsEditModalOpen(true);
                    }}
                >
                  Edit Staff
                </Button>
                <Button
                    danger
                    icon={<DeleteOutlined/>}
                    onClick={() => setIsDeleteModalOpen(true)}
                >
                  Delete Staff
                </Button>
              </>
          )}
        </div>
      </div>

      {/* Staff Information */}
      <Descriptions bordered column={2} style={{ marginBottom: 24 }}>
        <Descriptions.Item label="Title">{staff?.title}</Descriptions.Item>
        <Descriptions.Item label="Username">{staff?.username}</Descriptions.Item>
        <Descriptions.Item label="First Name">{staff?.firstName}</Descriptions.Item>
        <Descriptions.Item label="Last Name">{staff?.lastName}</Descriptions.Item>
        <Descriptions.Item label="Email">
          <a href={`mailto:${staff?.email}`}>{staff?.email}</a>
        </Descriptions.Item>
        <Descriptions.Item label="Department">{staff?.department}</Descriptions.Item>
      </Descriptions>

      {/* Teaching Statistics */}
      {modules.length > 0 && calculateStatistics() && (
        <Card title="Teaching Overview" style={{ marginBottom: 24 }}>
          <Row gutter={[16, 16]}>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f5ff' }}>
                <Statistic
                  title="Total Modules"
                  value={calculateStatistics().totalModules}
                  valueStyle={{ color: '#1890ff' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f6ffed' }}>
                <Statistic
                  title="Total Students"
                  value={calculateStatistics().totalStudents}
                  valueStyle={{ color: '#52c41a' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#fff0f6' }}>
                <Statistic
                  title="Average Pass Rate"
                  value={calculateStatistics().averagePassRate}
                  suffix="%"
                  valueStyle={{ color: '#eb2f96' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f2ff' }}>
                <Statistic
                  title="Average Score"
                  value={calculateStatistics().averageScore}
                  suffix="%"
                  valueStyle={{ color: '#722ed1' }}
                />
              </Card>
            </Col>
          </Row>
        </Card>
      )}

      {/* Teaching Modules Section */}
      <div style={{ marginBottom: 16 }}>
        <h3>Teaching Modules</h3>
      </div>
      <Table
        columns={moduleColumns}
        dataSource={modules}
        rowKey="code"
        pagination={false}
        loading={loading}
        onRow={(record) => ({
          onClick: () => setSelectedKey(['moduleDetail', record.code]),
          style: { cursor: 'pointer' }
        })}
        rowClassName={() => 'clickable-row'}
      />

      {/* Edit Staff Modal */}
      <Modal
        title="Edit Staff Details"
        open={isEditModalOpen}
        onOk={handleEditSubmit}
        onCancel={() => setIsEditModalOpen(false)}
        confirmLoading={submitting}
        width={600}
      >
        <Form form={editForm} layout="vertical">
          <Form.Item name="firstName" label="First Name" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="lastName" label="Last Name" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="username" label="Username" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="title" label="Title" rules={[{ required: true }]}>
            <Select
              showSearch
              style={{ width: '100%' }}
              placeholder="Select or type a title"
              optionFilterProp="children"
              filterOption={(input, option) =>
                (option?.children || '').toLowerCase().includes(input.toLowerCase())
              }
              onSearch={(value) => {
                if (value && !titles.includes(value)) {
                  setTitles([...titles, value]);
                }
              }}
            >
              {titles.map((title) => (
                <Option key={title} value={title}>
                  {title}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="department" label="Department" rules={[{ required: true }]}>
            <Select
              showSearch
              style={{ width: '100%' }}
              placeholder="Select or type a department"
              optionFilterProp="children"
              filterOption={(input, option) =>
                (option?.children || '').toLowerCase().includes(input.toLowerCase())
              }
              onSearch={(value) => {
                if (value && !departments.includes(value)) {
                  setDepartments([...departments, value]);
                }
              }}
            >
              {departments.map((department) => (
                <Option key={department} value={department}>
                  {department}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="password"
            label="Password"
            rules={[
              { min: 6, message: 'Password must be at least 6 characters!' }
            ]}
          >
            <Input.Password placeholder="Leave blank to keep current password" />
          </Form.Item>
        </Form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        title="Delete Staff"
        open={isDeleteModalOpen}
        onOk={handleDelete}
        onCancel={() => setIsDeleteModalOpen(false)}
        okText="Delete"
        okButtonProps={{ danger: true }}
      >
        <p>Are you sure you want to delete this staff member?</p>
        <p>This will permanently delete:</p>
        <ul>
          <li>Staff profile and personal information</li>
          <li>All module assignments</li>
          <li>All associated records</li>
        </ul>
        <p style={{ color: '#ff4d4f', marginTop: 16 }}>
          This action cannot be undone.
        </p>
      </Modal>
    </Content>
  );
};

export default AcademicStaffRecord;
