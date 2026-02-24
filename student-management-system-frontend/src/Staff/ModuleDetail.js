import React, {useEffect, useState} from 'react';
import {
  Button,
  Card,
  Col,
  Descriptions,
  Form,
  Input,
  InputNumber,
  Layout,
  message,
  Modal,
  Progress,
  Row,
  Select,
  Spin,
  Statistic,
  Table
} from 'antd';
import {DeleteOutlined, EditOutlined, ReloadOutlined} from '@ant-design/icons';
import './Contents.css';
import api from "../api";
import {debounce} from 'lodash';

const {Content} = Layout;
const {Option} = Select;

const ModuleDetail = ({moduleId, setSelectedKey, setModuleInfo, readOnly = false}) => {
  const [module, setModule] = useState(null);
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isDeleteModalVisible, setIsDeleteModalVisible] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editForm] = Form.useForm();
  const [staffList, setStaffList] = useState([]);
  const [staffLoading, setStaffLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const fetchModuleData = async () => {
    if (!moduleId) return;
    setLoading(true);
    try {
      const response = await api.get(`/api/module/${moduleId}`);
      const moduleData = response.data.data;
      setModule(moduleData);
      setModuleInfo({
        code: moduleData.code,
        name: moduleData.name
      });

      // Fetch records for this module
      const recordsResponse = await api.get('/api/record', {
        params: {
          moduleCode: response.data.data.code,
          current: 1,
          size: 100
        }
      });
      setRecords(recordsResponse.data?.data?.records || []);
    } catch (error) {
      message.error('Failed to fetch module data');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const fetchStaffList = async (searchText = '') => {
    setStaffLoading(true);
    try {
      const response = await api.get('/api/staff/list-staffs', {
        params: {
          current: 1,
          size: 100,
          firstName: searchText,
          lastName: searchText
        }
      });

      if (response.data?.code === 200) {
        setStaffList(response.data.data.records || []);
      }
    } catch (error) {
      console.error('Error fetching staff list:', error);
      message.error('Failed to load staff list');
    } finally {
      setStaffLoading(false);
    }
  };

  const handleEdit = async () => {
    try {
      const values = await editForm.validateFields();
      setSubmitting(true);

      const response = await api.put(`/api/module/edit/${moduleId}`, {
        code: values.code,
        name: values.name,
        credits: values.credits,
        mnc: values.mnc,
        staffId: values.staffId
      });

      if (response.data?.code === 200) {
        message.success('Module updated successfully');
        setIsEditModalOpen(false);
        fetchModuleData();
      } else {
        throw new Error(response.data?.message || 'Failed to update module');
      }
    } catch (error) {
      console.error('Error updating module:', error);
      message.error(error.response?.data?.message || 'Failed to update module');
    } finally {
      setSubmitting(false);
    }
  };

  const findCurrentStaff = () => {
    return staffList.find(staff =>
        staff.firstName === module?.staffFirstName &&
        staff.lastName === module?.staffLastName
    );
  };

  const handleEditClick = () => {
    const currentStaff = findCurrentStaff();
    editForm.setFieldsValue({
      code: module?.code,
      name: module?.name,
      credits: module?.credits,
      mnc: module?.mnc,
      staffId: currentStaff?.id
    });
    setIsEditModalOpen(true);
  };

  useEffect(() => {
    fetchModuleData();
    fetchStaffList();
  }, [moduleId]);

  const handleDelete = async () => {
    try {
      const response = await api.delete(`/api/module/delete/${module?.code}`);

      if (response.data?.code === 200) {
        message.success('Module deleted successfully');
        setIsDeleteModalVisible(false);
        setSelectedKey('3'); // Navigate back to module list
      } else {
        throw new Error(response.data?.message || 'Failed to delete module');
      }
    } catch (error) {
      console.error('Error deleting module:', error);
      message.error(error.response?.data?.message || 'Failed to delete module');
    }
  };

  const calculateModuleStatistics = (records) => {
    if (!records || records.length === 0) {
      return {
        totalExams: 0,
        averageScore: 0,
        averagePassRate: 0,
        trend: {
          scores: [],
          passRates: [],
          participation: []
        },
        improvement: {
          score: 0,
          passRate: 0,
          participation: 0
        }
      };
    }

    // Sort records by date
    const sortedRecords = [...records].sort((a, b) => new Date(a.date) - new Date(b.date));

    // Calculate averages
    const averageScore = records.reduce((acc, r) => acc + r.averageScore, 0) / records.length;
    const averagePassRate = records.reduce((acc, r) => acc + r.passRate, 0) / records.length;

    // Calculate trends
    const trend = {
      scores: sortedRecords.map(r => r.averageScore),
      passRates: sortedRecords.map(r => r.passRate * 100),
      participation: sortedRecords.map(r => r.numberOfCandidates)
    };

    // Calculate improvements (comparing last two exams)
    const improvement = {
      score: sortedRecords.length > 1
          ? sortedRecords[sortedRecords.length - 1].averageScore - sortedRecords[sortedRecords.length - 2].averageScore
          : 0,
      passRate: sortedRecords.length > 1
        ? (sortedRecords[sortedRecords.length - 1].passRate - sortedRecords[sortedRecords.length - 2].passRate) * 100
        : 0,
      participation: sortedRecords.length > 1
        ? sortedRecords[sortedRecords.length - 1].numberOfCandidates - sortedRecords[sortedRecords.length - 2].numberOfCandidates
        : 0
    };

    return {
      totalExams: records.length,
      averageScore,
      averagePassRate,
      trend,
      improvement
    };
  };

  const recordColumns = [
    {
      title: 'Record ID',
      dataIndex: 'id',
      key: 'id',
      width: 120
    },
    {
      title: 'Date',
      dataIndex: 'date',
      key: 'date',
      width: 150
    },
    {
      title: 'Number of Students',
      dataIndex: 'numberOfCandidates',
      key: 'numberOfCandidates',
      width: 150
    },
    {
      title: 'Average Score',
      dataIndex: 'averageScore',
      key: 'averageScore',
      width: 150,
      render: (score) => score?.toFixed(2) || '-'
    },
    {
      title: 'Pass Rate',
      dataIndex: 'passRate',
      key: 'passRate',
      width: 150,
      render: (rate) => rate ? `${(rate * 100).toFixed(1)}%` : '-'
    }
  ];

  const handleStaffSearch = debounce((value) => {
    fetchStaffList(value);
  }, 500);

  return (
    <Content style={{ margin: '24px 16px 0', padding: 24, background: '#fff' }}>
      {/* Header Section */}
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2>Module Detail</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button icon={<ReloadOutlined />} onClick={fetchModuleData} loading={loading} />
          {!readOnly && (
              <>
                <Button
                    type="primary"
                    icon={<EditOutlined/>}
                    onClick={handleEditClick}
                >
                  Edit Module
                </Button>
                <Button
                    danger
                    icon={<DeleteOutlined/>}
                    onClick={() => setIsDeleteModalVisible(true)}
                >
                  Delete Module
                </Button>
              </>
          )}
        </div>
      </div>

      {/* Module Information Section */}
      <Descriptions bordered column={2} style={{ marginBottom: 24 }}>
        <Descriptions.Item label="Module Code">{module?.code}</Descriptions.Item>
        <Descriptions.Item label="Module Name">{module?.name}</Descriptions.Item>
        <Descriptions.Item label="Credits">{module?.credits}</Descriptions.Item>
        <Descriptions.Item label="Mandatory Non-Condonable">
          {module?.mnc ? 'Yes' : 'No'}
        </Descriptions.Item>
        <Descriptions.Item label="Module Leader">
          {`${module?.staffFirstName || ''} ${module?.staffLastName || ''}`.trim() || '-'}
        </Descriptions.Item>
        <Descriptions.Item label="Number of Exams">
          {records?.length || 0}
        </Descriptions.Item>
      </Descriptions>

      {/* Module Statistics Section */}
      {records.length > 0 && (
        <div style={{ marginBottom: 24 }}>
          <h3>Module Performance Analysis</h3>

          {/* Overall Statistics */}
          <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
            <Col span={8}>
              <Card>
                <Statistic
                  title="Average Score Across All Exams"
                  value={calculateModuleStatistics(records).averageScore}
                  suffix="%"
                  precision={2}
                  valueStyle={{ color: '#1890ff' }}
                />
                <div style={{ marginTop: 8, fontSize: '12px', color: calculateModuleStatistics(records).improvement.score >= 0 ? '#52c41a' : '#f5222d' }}>
                  {calculateModuleStatistics(records).improvement.score > 0 ? '↑' : '↓'}
                  {Math.abs(calculateModuleStatistics(records).improvement.score).toFixed(2)}% from last exam
                </div>
              </Card>
            </Col>
            <Col span={8}>
              <Card>
                <Statistic
                  title="Average Pass Rate"
                  value={calculateModuleStatistics(records).averagePassRate * 100}
                  suffix="%"
                  precision={1}
                  valueStyle={{ color: '#52c41a' }}
                />
                <div style={{ marginTop: 8, fontSize: '12px', color: calculateModuleStatistics(records).improvement.passRate >= 0 ? '#52c41a' : '#f5222d' }}>
                  {calculateModuleStatistics(records).improvement.passRate > 0 ? '↑' : '↓'}
                  {Math.abs(calculateModuleStatistics(records).improvement.passRate).toFixed(1)}% from last exam
                </div>
              </Card>
            </Col>
            <Col span={8}>
              <Card>
                <Statistic
                    title="Student Participation Change"
                    value={calculateModuleStatistics(records).improvement.participation}
                    prefix={calculateModuleStatistics(records).improvement.participation > 0 ? '+' : ''}
                    valueStyle={{
                      color: calculateModuleStatistics(records).improvement.participation > 0
                          ? '#52c41a'
                          : calculateModuleStatistics(records).improvement.participation < 0
                              ? '#f5222d'
                              : '#1890ff'
                    }}
                />
                <div style={{marginTop: 8, fontSize: '12px'}}>
                  Compared to last exam
                </div>
              </Card>
            </Col>
          </Row>

          {/* Performance Trend */}
          <Card title="Performance Trends" style={{ marginBottom: 24 }}>
            <Row gutter={[16, 16]}>
              {records.map((record, index) => (
                <Col span={24} key={record.id}>
                  <div style={{ marginBottom: 16 }}>
                    <div style={{ marginBottom: 8, display: 'flex', justifyContent: 'space-between' }}>
                      <span>Exam {index + 1} ({record.date})</span>
                      <span>{record.numberOfCandidates} students</span>
                    </div>
                    <Progress
                      percent={parseFloat(record.averageScore)}
                      status={record.averageScore >= 40 ? "success" : "exception"}
                      strokeColor={{
                        '0%': '#108ee9',
                        '100%': record.averageScore >= 40 ? '#52c41a' : '#f5222d',
                      }}
                      format={(percent) => {
                        const value = parseFloat(percent);
                        return isNaN(value) ? '0%' : `${value.toFixed(1)}%`;
                      }}
                    />
                    <div style={{ fontSize: '12px', color: '#666' }}>
                      Pass Rate: {(record.passRate * 100).toFixed(1)}%
                    </div>
                  </div>
                </Col>
              ))}
            </Row>
          </Card>
        </div>
      )}

      {/* Records Section */}
      <div style={{marginBottom: 16}}>
        <h3>Examination Records</h3>
      </div>
      <Table
          columns={recordColumns}
          dataSource={records}
          rowKey="id"
          pagination={{pageSize: 10}}
          onRow={(record) => ({
            onClick: () => setSelectedKey(['recordDetail', record.id]),
            style: {cursor: 'pointer'}
          })}
          rowClassName={() => 'clickable-row'}
      />

      {/* Only render modals if not in readOnly mode */}
      {!readOnly && (
        <>
          <Modal
            title="Delete Module"
            open={isDeleteModalVisible}
            onOk={handleDelete}
            onCancel={() => setIsDeleteModalVisible(false)}
            okText="Delete"
            okButtonProps={{ danger: true }}
          >
            <p>Are you sure you want to delete this module?</p>
            <p>This will permanently delete:</p>
            <ul>
              <li>Module {module?.code} - {module?.name}</li>
              <li>All examination records for this module</li>
              <li>All student grades associated with this module</li>
              <li>All module assignments and references</li>
            </ul>
            <p style={{ color: '#ff4d4f', marginTop: 16 }}>
              This action cannot be undone.
            </p>
          </Modal>

          <Modal
            title="Edit Module"
            open={isEditModalOpen}
            onOk={handleEdit}
            onCancel={() => setIsEditModalOpen(false)}
            confirmLoading={submitting}
            width={600}
          >
            <Form form={editForm} layout="vertical">
              <Form.Item
                name="code"
                label="Module Code"
                rules={[{ required: true, message: 'Please enter the module code' }]}
              >
                <Input disabled />
              </Form.Item>
              <Form.Item
                name="name"
                label="Module Name"
                rules={[{ required: true, message: 'Please enter the module name' }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                name="credits"
                label="Credits"
                rules={[{ required: true, message: 'Please enter the credits' }]}
              >
                <InputNumber min={0} max={100} style={{ width: '100%' }} />
              </Form.Item>
              <Form.Item
                name="mnc"
                label="Mandatory Non-Condonable"
                rules={[{ required: true, message: 'Please select if the module is mandatory non-condonable' }]}
              >
                <Select>
                  <Option value={true}>Yes</Option>
                  <Option value={false}>No</Option>
                </Select>
              </Form.Item>
              <Form.Item
                name="staffId"
                label="Module Leader"
                rules={[{ required: true, message: 'Please select a module leader' }]}
              >
                <Select
                  showSearch
                  placeholder="Search for staff"
                  loading={staffLoading}
                  onSearch={handleStaffSearch}
                  filterOption={(input, option) => {
                    const staff = staffList.find(s => s.id === option.value);
                    return (
                      staff?.firstName?.toLowerCase().includes(input.toLowerCase()) ||
                      staff?.lastName?.toLowerCase().includes(input.toLowerCase()) ||
                      staff?.title?.toLowerCase().includes(input.toLowerCase()) ||
                      staff?.department?.toLowerCase().includes(input.toLowerCase())
                    );
                  }}
                  notFoundContent={staffLoading ? <Spin size="small" /> : null}
                >
                  {staffList.map(staff => (
                    <Option key={staff.id} value={staff.id}>
                      {`${staff.firstName} ${staff.lastName} (${staff.title}) - ${staff.department}`}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Form>
          </Modal>
        </>
      )}
    </Content>
  );
};

export default ModuleDetail;
