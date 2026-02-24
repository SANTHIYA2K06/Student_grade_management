import React, {useEffect, useState} from 'react';
import {Button, Form, Input, InputNumber, Layout, message, Modal, Select, Spin, Table} from 'antd';
import {PlusOutlined, ReloadOutlined, SearchOutlined} from '@ant-design/icons';
import api from "../api";
import './Contents.css';
import {debounce} from 'lodash';

const {Content} = Layout;
const {Option} = Select;

const ModuleManagement = ({setSelectedKey, readOnly = false}) => {
  const [filteredModules, setFilteredModules] = useState([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();
  const [staffList, setStaffList] = useState([]);
  const [staffLoading, setStaffLoading] = useState(false);
  const [searchEntity, setSearchEntity] = useState('code');

  // Fetch modules
  const fetchModules = async (params = {}) => {
    setLoading(true);
    try {
      const searchParams = {
        current: params.current || pagination.current,
        size: params.pageSize || pagination.pageSize,
        code: '',
        name: '',
        leader: ''
      };

      // Handle search based on selected entity
      if (params.searchText) {
        const searchValue = params.searchText.trim();
        switch (params.searchEntity || searchEntity) {
          case 'code':
            searchParams.code = searchValue;
            break;
          case 'name':
            searchParams.name = searchValue;
            break;
          case 'leader':
            searchParams.leader = searchValue;
            break;
          default:
            break;
        }
      }

      const response = await api.get('/api/module/list', {
        params: searchParams
      });

      if (response.data?.data?.records) {
        setFilteredModules(response.data.data.records);
        setPagination({
          ...pagination,
          total: response.data.data.total,
          current: response.data.data.current,
          pageSize: response.data.data.size,
          showSizeChanger: true,

        });
      }
    } catch (error) {
      console.error('Error fetching modules:', error);
      message.error('Failed to load modules');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchModules();
  }, []);

  const showAddModuleModal = () => {
    setIsModalOpen(true);
  };

  const handleCancel = () => {
    setIsModalOpen(false);
    form.resetFields();
  };

  const handleAddModule = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);

      const formattedValues = {
        code: values.code,
        name: values.name,
        credits: values.credits,
        mnc: values.mnc,
        staffId: values.staffId // Make sure to use staffId here
      };

      await api.post('/api/module/add', formattedValues);
      message.success('Module added successfully');
      setIsModalOpen(false);
      form.resetFields();
      fetchModules(); // Refresh the module list
    } catch (error) {
      console.error('Error adding module:', error);
      if (error.errorFields) {
        return; // Form validation error
      }
      const errorMsg = error.response?.data?.message || 'Failed to add module';
      message.error(errorMsg);
    } finally {
      setSubmitting(false);
    }
  };

  const handleSearch = debounce((value) => {
    fetchModules({
      searchText: value,
      searchEntity: searchEntity,
      current: 1 // Reset to first page when searching
    });
  }, 500);

  const fetchStaffList = async (searchText = '') => {
    setStaffLoading(true);
    try {
      const response = await api.get('/api/staff/list-staffs', {
        params: {
          current: 1,
          size: 100,
          leader: searchText
        }
      });

      if (response.data?.data?.records) {
        setStaffList(response.data.data.records);
      }
    } catch (error) {
      console.error('Error fetching staff list:', error);
      message.error('Failed to load staff list');
    } finally {
      setStaffLoading(false);
    }
  };

  useEffect(() => {
    fetchStaffList();
  }, []);

  const handleStaffSearch = debounce((value) => {
    fetchStaffList(value);
  }, 500);

  const columns = [
    {
      title: 'Module Code',
      dataIndex: 'code',
      key: 'code',
      width: 150,
    },
    {
      title: 'Module Name',
      dataIndex: 'name',
      key: 'name',
      width: 200,
    },
    {
      title: 'Credits',
      dataIndex: 'credits',
      key: 'credits',
      width: 100,
    },
    {
      title: 'Mandatory Non-Condonable',
      dataIndex: 'mnc',
      key: 'mnc',
      render: (mnc) => (mnc ? 'Yes' : 'No'),
      width: 150,
    },
    {
      title: 'Module Leader',
      key: 'leader',
      width: 200,
      render: (_, record) => (
          record.staffFirstName && record.staffLastName
          ? `${record.staffFirstName} ${record.staffLastName}`
          : '-'
      )
    },
  ];

  return (
    <Content style={{ margin: '24px 16px 0', padding: 24, background: '#fff' }}>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h2>Module Management</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button icon={<ReloadOutlined />} onClick={() => fetchModules()} />
          <Input.Group compact>
            <Select
              defaultValue="code"
              value={searchEntity}
              onChange={setSearchEntity}
              style={{ width: 120 }}
            >
              <Option value="name">Name</Option>
              <Option value="code">Code</Option>
              <Option value="leader">Leader</Option>
            </Select>
            <Input
              placeholder={`Search by module ${searchEntity}`}
              prefix={<SearchOutlined />}
              onChange={(e) => handleSearch(e.target.value)}
              style={{ width: 250 }}
              allowClear
            />
          </Input.Group>
          {!readOnly && (
            <Button type="primary" icon={<PlusOutlined />} onClick={showAddModuleModal}>
              Add Module
            </Button>
          )}
        </div>
      </div>
      <Table
        columns={columns.filter(col => !readOnly || col.key !== 'actions')} // Remove actions column if readOnly
        dataSource={filteredModules}
        loading={loading}
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          showSizeChanger: true,
          onChange: (page, pageSize) => {
            fetchModules({ current: page, pageSize });
          },
        }}
        rowClassName={() => 'clickable-row'}
        onRow={(record) => ({
          onClick: () => {
            if (record && record.code) {
              setSelectedKey(['moduleDetail', record.code]);
            }
          },
          style: { cursor: 'pointer' }
        })}
      />

      {!readOnly && (
        <Modal
          title="Add Module"
          open={isModalOpen}
          onOk={handleAddModule}
          onCancel={handleCancel}
          confirmLoading={submitting}
          width={600}
        >
          <Form form={form} layout="vertical">
            <Form.Item
              name="code"
              label="Module Code"
              rules={[{ required: true, message: 'Please enter the module code' }]}
            >
              <Input />
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
                filterOption={false}
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
      )}
    </Content>
  );
};

export default ModuleManagement;
