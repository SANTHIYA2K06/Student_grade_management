import React, {useCallback, useEffect, useState} from 'react';
import {Button, DatePicker, Form, Input, Layout, message, Modal, Popover, Select, Table, Upload} from 'antd';
import {
  DownloadOutlined,
  FilterOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  UploadOutlined
} from '@ant-design/icons';
import {debounce} from 'lodash';
import './Contents.css';
import api from "../api";

const {Content} = Layout;
const {Option} = Select;

const StudentManagement = ({onStudentClick, readOnly = false}) => {
  const [filteredStudents, setFilteredStudents] = useState([]);
  const [pagination, setPagination] = useState({current: 1, pageSize: 10, total: 0});
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();
  const [searchText, setSearchText] = useState('');
  const [programmeFilter, setProgrammeFilter] = useState('');
  const [degreeLevelFilter, setDegreeLevelFilter] = useState('');
  const [programmeOptions, setProgrammeOptions] = useState([]);
  const [searchEntity, setSearchEntity] = useState('id');
  const [levelOptions] = useState(['BSc', 'BEng', 'BA', 'MSc', 'MEng', 'MA', 'PhD', 'Dip']);

  const { current, pageSize } = pagination;

  const fetchStudents = useCallback(async (params = {}) => {
    setLoading(true);
    try {
      const searchParams = {
        current: params.current || current,
        size: params.pageSize || pageSize,
        id: '',
        firstName: '',
        lastName: '',
        username: '',
        programOfStudy: '',
        email: ''
      };

      if (params.searchText) {
        const searchValue = params.searchText.trim();
        switch (params.searchEntity || searchEntity) {
          case 'id':
            searchParams.id = searchValue;
            break;
          case 'name':
            searchParams.fullName = searchValue;
            break;
          case 'username':
            searchParams.username = searchValue;
            break;
          default:
            break;
        }
      }

      if (params.programOfStudy) {
        searchParams.programOfStudy = params.programOfStudy;
      }

      const response = await api.get('/api/student/list-students', {
        params: searchParams
      });

      if (response.data?.data?.records) {
        const records = response.data.data.records;
        setFilteredStudents(records);
        setPagination({
          total: response.data.data.total,
          current: response.data.data.current,
          pageSize: response.data.data.size,
          showSizeChanger: true,
        });
      }
    } catch (error) {
      console.error('Error fetching students:', error);
      message.error('Failed to load students');
    } finally {
      setLoading(false);
    }
  },[current, pageSize, searchEntity]);

  const fetchProgrammes = useCallback(async () => {
    try {
      const response = await api.get('/api/student/list-programs');
      if (response.data?.code === 200) {
        const programmes = (response.data.data || []).map(prog =>
          prog.replace(/^(BSc|BEng|BA|MSc|MEng|MA|PhD|Dip)\s+/, '')
        );
        setProgrammeOptions([...new Set(programmes)]);
      } else {
        throw new Error(response.data?.message || 'Failed to fetch programmes');
      }
    } catch (error) {
      console.error('Error fetching programmes:', error);
      message.error(error.response?.data?.message || 'Failed to load programme options');
    }
  }, []);


  useEffect(() => {
    fetchStudents();
    fetchProgrammes();
  }, [fetchStudents, fetchProgrammes]);

  const handleCancel = () => {
    setIsModalOpen(false);
    form.resetFields();
  };

  const handleAddStudent = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);

      const fullProgramOfStudy = `${values.levelOfStudy} ${values.programOfStudy}`;

      const formattedValues = {
        firstName: values.firstName,
        lastName: values.lastName,
        birthDate: values.birthDate.format('YYYY-MM-DD'),
        email: values.email,
        department: values.department,
        graduationYear: parseInt(values.graduationYear),
        programOfStudy: fullProgramOfStudy,
        username: values.username,
        password: values.password
      };

      const response = await api.post('/api/student/add-student', formattedValues);

      if (response.data?.code === 200) {
        message.success('Student added successfully');
        setIsModalOpen(false);
        form.resetFields();
        await fetchStudents();
      } else {
        message.error(response.data?.message);
      }
    } catch (error) {
      if (error.errorFields) {
        return;
      }
      console.error('Error adding student:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Unknown error';
      message.error('Failed to add student: ' + errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  const handleFileUpload = async ({ file, onSuccess, onError }) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await api.post('/api/student/import-students', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.data?.code === 200) {
        message.success('Students imported successfully');
        onSuccess('ok');
        await fetchStudents();
      } else {
        throw new Error(response.data?.message || 'Import failed');
      }
    } catch (error) {
      console.error('Error importing students:', error);
      message.error(error.response?.data?.message || 'Failed to import students');
      onError(error);
    }
  };

  const handleSearch = debounce((value) => {
    setSearchText(value)
    fetchStudents({
      searchText: value,
      searchEntity: searchEntity,
      current: 1 // Reset to first page when searching
    });
  }, 500);

  const handleProgrammeFilter = (value) => {
    setProgrammeFilter(value);
    if (!value) {
      fetchStudents({
        searchText,
        programOfStudy: degreeLevelFilter, // Keep level filter if exists
        current: 1
      });
    } else {
      const fullProgramme = degreeLevelFilter ? `${degreeLevelFilter} ${value}` : value;
      fetchStudents({
        programOfStudy: fullProgramme,
        searchText,
        current: 1
      });
    }
  };

  const handleDegreeLevelFilter = (value) => {
    setDegreeLevelFilter(value);
    if (!value) {
      fetchStudents({
        searchText,
        programOfStudy: programmeFilter,  // Keep programme filter if exists
        current: 1
      });
    } else {
      const fullProgramme = programmeFilter ? `${value} ${programmeFilter}` : `${value}`;
      fetchStudents({
        programOfStudy: fullProgramme,
        searchText,
        current: 1
      });
    }
  };

  const handleTableChange = (pagination) => {
    fetchStudents({
      current: pagination.current,
      pageSize: pagination.pageSize,
    });
  };

  const filterContent = (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
      <Select
        placeholder="Filter by Level"
        style={{ width: 200 }}
        value={degreeLevelFilter}
        onChange={handleDegreeLevelFilter}
        allowClear
      >
        <Option value="">All Levels</Option>
        {levelOptions.map(level => (
          <Option key={level} value={level}>{level}</Option>
        ))}
      </Select>

      <Select
        placeholder="Filter by Programme"
        style={{ width: 200 }}
        value={programmeFilter}
        onChange={handleProgrammeFilter}
        allowClear
      >
        <Option value="">All Programmes</Option>
        {[...new Set(
          programmeOptions.map(prog => prog.replace(/^(BSc|BEng|BA|MSc|MEng|MA|PhD|Dip)\s+/, ''))
        )].map(programme => (
          <Option key={programme} value={programme}>{programme}</Option>
        ))}
      </Select>

      <Button onClick={() => {
        setProgrammeFilter('');
        setDegreeLevelFilter('');
        fetchStudents({
          current: 1,
          pageSize: pagination.pageSize
        });
      }} style={{ marginTop: '8px' }}>
        Reset Filters
      </Button>
    </div>
  );

  const csvTemplateContent = (
    <div style={{ maxWidth: '300px' }}>
      <p>Please download and follow the CSV template format for importing student data:</p>
      <a
        href="./files/StudentDataTemplate.csv"
        download="Student Data Template.csv"
        style={{
          color: '#1890ff',
          textDecoration: 'underline',
          display: 'flex',
          alignItems: 'center',
          gap: '8px'
        }}
      >
        <DownloadOutlined /> Download Template
      </a>
      <p style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
        Note: The CSV file must include:
        <ul style={{ marginTop: '4px', paddingLeft: '16px' }}>
          <li>firstName: First name of the student</li>
          <li>lastName: Last name of the student</li>
          <li>birthDate: Format YYYY-MM-DD</li>
          <li>email: Valid email</li>
          <li>department: Department of the student</li>
          <li>graduationYear: Graduation year of the student</li>
          <li>programOfStudy: Programme of study of the student
            <ul style={{ marginTop: '4px', paddingLeft: '16px' }}>
              <li>Format: [level] [programme]</li>
              <li>Example: BSc Computer Science</li>
            </ul>
          </li>
        </ul>
      </p>
    </div>
  );

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 100 },
    { title: 'First Name', dataIndex: 'firstName', key: 'firstName', width: 150 },
    { title: 'Last Name', dataIndex: 'lastName', key: 'lastName', width: 150 },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      width: 300,
      render: (email) => email ? (
        <a href={`mailto:${email}`} style={{ color: '#1890ff' }}>
          {email}
        </a>
      ) : '-'
    },
    {
      title: 'Programme of Study',
      dataIndex: 'programOfStudy',
      key: 'programOfStudy',
      width: 200,
      render: (text) => {
        if (!text) return '-';
        const parts = text.split(' ');
        const level = parts[0];
        const programme = parts.slice(1).join(' ');
        return (
          <span>
            <strong>{level}</strong> {programme}
          </span>
        );
      }
    },
  ];


  return (
    <Content style={{ margin: '24px 16px 0', padding: 24, background: '#fff' }}>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h2>Student Management</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button icon={<ReloadOutlined />} onClick={() => fetchStudents()} />

          <Input.Group compact>
            <Select
              defaultValue="id"
              value={searchEntity}
              onChange={setSearchEntity}
              style={{ width: 120 }}
            >
              <Option value="id">Student ID</Option>
              <Option value="name">Name</Option>
              <Option value="username">Username</Option>
            </Select>
            <Input
              placeholder={`Search by ${searchEntity}`}
              prefix={<SearchOutlined />}
              onChange={(e) => handleSearch(e.target.value)}
              style={{ width: 250 }}
              allowClear
            />
          </Input.Group>
          <Popover content={filterContent} title="Filters" trigger="click">
            <Button icon={<FilterOutlined />}>Filters</Button>
          </Popover>
          {!readOnly && (
            <>
              <Popover
                content={csvTemplateContent}
                title="CSV Template"
                trigger="hover"
                placement="bottom"
              >
                <Upload
                  accept=".csv"
                  showUploadList={false}
                  customRequest={handleFileUpload}
                  beforeUpload={(file) => {
                    const isCsv = file.type === 'text/csv' || file.name.endsWith('.csv');
                    if (!isCsv) {
                      message.error('You can only upload CSV files!');
                      return Upload.LIST_IGNORE;
                    }
                    return true;
                  }}
                >
                  <Button icon={<UploadOutlined />}>Import from CSV</Button>
                </Upload>
              </Popover>
              <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsModalOpen(true)}>
                Add Student
              </Button>
            </>
          )}
        </div>
      </div>
      <Table
        columns={columns}
        dataSource={filteredStudents}
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          showSizeChanger: true,
        }}
        loading={loading}
        onChange={handleTableChange}
        rowClassName={() => 'clickable-row'}
        onRow={(record) => ({
          onClick: () => {
            onStudentClick({
              ...record,
              readOnly: readOnly // Pass readOnly prop to StudentRecord
            });
          },
        })}
      />
      {!readOnly && (
        <Modal
          title="Add Student"
          open={isModalOpen}
          confirmLoading={submitting}
          onOk={handleAddStudent}
          onCancel={handleCancel}
        >
          <Form form={form} layout="vertical">
            <Form.Item
              name="firstName"
              label="First Name"
              rules={[{ required: true, whitespace: true, message: 'Please enter the first name' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="lastName"
              label="Last Name"
              rules={[{ required: true, whitespace: true, message: 'Please enter the last name' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="birthDate"
              label="Date of Birth"
              rules={[{ required: true, message: 'Please select the date of birth' }]}
            >
              <DatePicker style={{ width: '100%' }} />
            </Form.Item>
            <Form.Item
              name="department"
              label="Department"
              rules={[{ required: true, whitespace: true, message: 'Please enter the department' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="email"
              label="Email"
              rules={[
                { required: true, message: 'Please enter the email' },
                { type: 'email', message: 'Please enter a valid email' }
              ]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="graduationYear"
              label="Graduation Year"
              rules={[{ required: true, message: 'Please select the graduation year' }]}
            >
              <Select placeholder="Select Graduation Year">
                {Array.from({ length: 21 }, (_, i) => {
                  const year = new Date().getFullYear() + i;
                  return (
                    <Select.Option key={year} value={year}>
                      {year}
                    </Select.Option>
                  );
                })}
              </Select>
            </Form.Item>

            <Form.Item
              name="levelOfStudy"
              label="Level of Study"
              rules={[{ required: true, message: 'Please select the level of study' }]}
            >
              <Select
                placeholder="Select level of study"
                style={{ width: '100%' }}
              >
                {levelOptions.map((level) => (
                  <Option key={level} value={level}>
                    {level}
                  </Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item
              name="programOfStudy"
              label="Programme of Study"
              rules={[{ required: true, message: 'Please select the programme of study' }]}
    >
              <Select
                showSearch
                placeholder="Select or type programme"
                optionFilterProp="children"
                filterOption={(input, option) =>
                  (option?.children || '').toLowerCase().includes(input.toLowerCase())
                }
                onSearch={(value) => {
                  const cleanValue = value.replace(/^(Bachelor|Master|PhD|Diploma)\s+/, '');
                  if (cleanValue && !programmeOptions.some(p => p.replace(/^(Bachelor|Master|PhD|Diploma)\s+/, '') === cleanValue)) {
                    setProgrammeOptions([...programmeOptions, cleanValue]);
                  }
                }}
              >
                {programmeOptions.map((programme) => (
          <Option
            key={programme}
            value={programme.replace(/^(Bachelor|Master|PhD|Diploma)\s+/, '')}
          >
            {programme.replace(/^(Bachelor|Master|PhD|Diploma)\s+/, '')}
                  </Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item
              name="username"
              label="Username"
              rules={[{ required: true, whitespace: true, message: 'Please enter the username' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              name="password"
              label="Password"
              rules={[
                { required: true, message: 'Please enter the password' },
                { min: 6, message: 'Password must be at least 6 characters' }
              ]}
            >
              <Input.Password />
            </Form.Item>
          </Form>
        </Modal>
      )}
    </Content>
  );
};

export default StudentManagement;
